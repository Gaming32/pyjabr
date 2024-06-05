import sys
from importlib.abc import MetaPathFinder, Loader
from importlib.machinery import ModuleSpec
from types import ModuleType
from typing import Sequence

import _java

JAVA_PACKAGE_PREFIX = 'java.'


class FakeJavaClass:
    name: str
    _id: int

    def __init__(self, name: str, id: int) -> None:
        self.name = name
        self._id = id

    def __repr__(self) -> str:
        return f'<java class {self.name}>'

    def __del__(self):
        _java.remove_class(self._id)


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
                raise AttributeError(name=name, obj=module)
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
