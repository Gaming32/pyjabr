import sys
from importlib.abc import MetaPathFinder, Loader
from importlib.machinery import ModuleSpec
from types import ModuleType
from typing import Sequence, Any

import _java

JAVA_PACKAGE_PREFIX = 'java.'
CONSTRUCTOR_NAME = '<init>'


class _JavaAttributeNotFoundType:
    def __repr__(self) -> str:
        return 'JavaAttributeNotFound'


JavaAttributeNotFound = _JavaAttributeNotFoundType()


class FakeJavaStaticMethod:
    owner: 'FakeJavaClass'
    name: str
    _id: int

    def __init__(self, owner: 'FakeJavaClass', name: str, id: int) -> None:
        self.owner = owner
        self.name = name
        self._id = id

    def __repr__(self) -> str:
        return f'<static Java method {self.owner.name}.{self.name}>'

    def __del__(self):
        _java.remove_static_method(self._id)


class FakeJavaClass:
    name: str
    _id: int
    attributes: dict[str, FakeJavaStaticMethod | int]

    def __init__(self, name: str, id: int) -> None:
        self.name = name
        self._id = id
        self.attributes = {}

    def __repr__(self) -> str:
        return f'<Java class {self.name}>'

    def __del__(self):
        _java.remove_class(self._id)
        for attr in self.attributes.values():
            if isinstance(attr, int):
                _java.remove_static_field(attr)
        self.attributes.clear()

    def __getattr__(self, name: str) -> FakeJavaStaticMethod | Any:
        try:
            attr = self.attributes[name]
        except KeyError:
            attr = _java.find_class_attribute(self, self._id, name)
            if isinstance(attr, _JavaAttributeNotFoundType):
                if name == CONSTRUCTOR_NAME:
                    raise AttributeError(
                        f'no public constructor for Java class {self.name}',
                        name=name, obj=self
                    ) from None
                raise AttributeError(
                    f"static attribute '{name}' not found on Java class {self.name}",
                    name=name, obj=self
                ) from None
            self.attributes[name] = attr
        if isinstance(attr, int):
            attr = _java.get_static_field(attr)
        return attr

    def __call__(self, *args: Any) -> Any:
        getattr(self, CONSTRUCTOR_NAME)(*args)


class JavaImportLoader(Loader):
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
    def find_spec(
            self,
            fullname: str,
            path: Sequence[str] | None,
            target: ModuleType | None = None
    ) -> ModuleSpec | None:
        if path:
            return None
        if not fullname.startswith(JAVA_PACKAGE_PREFIX):
            if fullname == JAVA_PACKAGE_PREFIX[:-1]:
                java_package = ''
            else:
                return None
        else:
            java_package = fullname.removeprefix(JAVA_PACKAGE_PREFIX)
        return ModuleSpec(fullname, JavaImportLoader(java_package), is_package=True)


sys.meta_path.append(JavaImportFinder())
