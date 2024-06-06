import os
import sys
from importlib.abc import MetaPathFinder, Loader
from importlib.machinery import ModuleSpec
from types import ModuleType, TracebackType
from typing import Sequence, Any, Self, Iterator

import _java

_JAVA_PACKAGE_PREFIX = 'java.'
_CONSTRUCTOR_NAME = '<init>'


class _JavaAttributeNotFoundType:
    __slots__ = ()

    def __repr__(self) -> str:
        return 'JavaAttributeNotFound'


JavaAttributeNotFound = _JavaAttributeNotFoundType()


class JavaError(RuntimeError):
    __slots__ = ('java_exception',)

    java_exception: 'FakeJavaObject'


class FakeJavaObject:
    __slots__ = ('_id', 'class_name', '_class_id')

    _id: int
    class_name: str
    _class_id: int

    def __init__(self, id: int, class_name: str, class_id: int) -> None:
        self._id = id
        self.class_name = class_name
        self._class_id = class_id

    def __eq__(self, other: Any) -> bool:
        if not isinstance(other, FakeJavaObject):
            return NotImplemented
        return self.equals(other)

    def identity_equals(self, other: 'FakeJavaObject') -> bool:
        return self._id == other._id

    def identity_hash(self) -> int:
        return _java.identity_hash(self._id)

    def __del__(self) -> None:
        _java.remove_object(self._id)
        _java.remove_class(self._class_id)

    def __getattr__(self, name: str) -> Any:
        raise NotImplementedError('FakeJavaObject.__getattr__')
        # raise AttributeError(
        #     f"instance attribute '{name}' not found on Java class {self.class_name}",
        #     name=name, obj=self
        # ) from None

    def __setattr__(self, key: str, value: Any) -> None:
        if key in FakeJavaObject.__slots__:
            return super().__setattr__(key, value)
        raise NotImplementedError('FakeJavaObject.__setattr__')

    def __iter__(self) -> Iterator[Any]:
        it = self.iterator()
        while it.hasNext():
            yield it.next()

    def __enter__(self) -> Self:
        if not hasattr(self, 'close'):
            raise TypeError(f'{self!r} is not closeable')
        return self

    def __exit__(self, exc_type: type[BaseException], exc_val: BaseException, exc_tb: TracebackType):
        self.close()

    def __str__(self) -> str:
        return _java.to_string(self._id)

    def __hash__(self) -> int:
        return _java.hash_code(self._id)

    def __repr__(self) -> str:
        return _java.identity_string(self._id)


class FakeJavaStaticMethod:
    __slots__ = ('owner_name', 'name', '_id')

    owner_name: str
    name: str
    _id: int

    def __init__(self, owner_name: str, name: str, id: int) -> None:
        self.owner_name = owner_name
        self.name = name
        self._id = id

    def __repr__(self) -> str:
        return f'<static Java method {self.owner_name}.{self.name}>'

    def __eq__(self, other: Any) -> bool:
        if not isinstance(other, FakeJavaStaticMethod):
            return NotImplemented
        return self._id == other._id

    def __del__(self) -> None:
        _java.remove_static_method(self._id)

    def __call__(self, *args: Any) -> Any:
        return _java.invoke_static_method(self._id, args)


class FakeJavaClass:
    __slots__ = ('class_name', '_id', '_attributes')

    class_name: str
    _id: int
    _attributes: dict[str, FakeJavaStaticMethod | int]

    def __init__(self, class_name: str, id: int) -> None:
        self.class_name = class_name
        self._id = id
        self._attributes = {}

    def __repr__(self) -> str:
        return f'<Java class {self.class_name}>'

    def __eq__(self, other: Any) -> bool:
        if not isinstance(other, FakeJavaClass):
            return NotImplemented
        return self._id == other._id

    def __del__(self) -> None:
        _java.remove_class(self._id)
        for attr in self._attributes.values():
            if isinstance(attr, int):
                _java.remove_static_field(attr)
        self._attributes.clear()

    def __getattr__(self, name: str) -> FakeJavaStaticMethod | Any:
        attr = self._get_attr(name)
        if isinstance(attr, int):
            attr = _java.get_static_field(attr)
        return attr

    def __setattr__(self, key: str, value: Any) -> None:
        if key in FakeJavaClass.__slots__:
            return super().__setattr__(key, value)
        attr = self._get_attr(key)
        if not isinstance(attr, int):
            raise TypeError(f'cannot assign to static method {self.class_name}.{key}')
        _java.set_static_field(attr, value)

    def _get_attr(self, name: str) -> FakeJavaStaticMethod | int:
        try:
            return self._attributes[name]
        except KeyError:
            attr = _java.find_class_attribute(self.class_name, self._id, name)
            if isinstance(attr, _JavaAttributeNotFoundType):
                if name == _CONSTRUCTOR_NAME:
                    raise AttributeError(
                        f'no public constructor for Java class {self.class_name}',
                        name=name, obj=self
                    ) from None
                raise AttributeError(
                    f"static attribute '{name}' not found on Java class {self.class_name}",
                    name=name, obj=self
                ) from None
            self._attributes[name] = attr
            return attr

    def __call__(self, *args: Any) -> Any:
        return getattr(self, _CONSTRUCTOR_NAME)(*args)

    def reflect_java(self) -> FakeJavaObject:
        return _java.reflect_class_object(self._id)


class JavaImportLoader(Loader):
    __slots__ = ('java_package',)

    java_package: str

    def __init__(self, java_package: str) -> None:
        self.java_package = java_package

    def exec_module(self, module: ModuleType) -> None:
        java_classes: dict[str, FakeJavaClass] = {}

        def module_getattr(name: str) -> FakeJavaClass:
            clazz = java_classes.get(name)
            if clazz is not None:
                return clazz
            full_name = f'{self.java_package}.{name}'
            class_id = _java.find_class(full_name)
            if class_id is None:
                raise AttributeError(f"Java class '{full_name}' not found", name=name, obj=module)
            clazz = FakeJavaClass(full_name, class_id)
            java_classes[name] = clazz
            return clazz

        def module_dir() -> list[str]:
            return list(java_classes.keys())

        module.__getattr__ = module_getattr
        module.__dir__ = module_dir

    def __repr__(self) -> str:
        return f'<Java package {self.java_package}>'


class JavaImportFinder(MetaPathFinder):
    __slots__ = ()

    def find_spec(
            self,
            fullname: str,
            path: Sequence[str] | None,
            target: ModuleType | None = None
    ) -> ModuleSpec | None:
        if path:
            return None
        if not fullname.startswith(_JAVA_PACKAGE_PREFIX):
            if fullname == _JAVA_PACKAGE_PREFIX[:-1]:
                java_package = ''
            else:
                return None
        else:
            java_package = fullname.removeprefix(_JAVA_PACKAGE_PREFIX)
        return ModuleSpec(fullname, JavaImportLoader(java_package), is_package=True)


sys.meta_path.append(JavaImportFinder())

# noinspection PyUnresolvedReferences,PyProtectedMember
del os._exit
