from typing import Any

from _java_init import FakeJavaClass, FakeJavaStaticMethod, _JavaAttributeNotFoundType


def find_class(name: str) -> int | None: ...

def remove_class(id: int) -> None: ...

def find_class_attribute(
    owner: FakeJavaClass,
    owner_id: int,
    name: str
) -> FakeJavaStaticMethod | int | _JavaAttributeNotFoundType: ...

def get_static_field(field_id: int) -> Any: ...

def remove_static_method(id: int) -> None: ...

def remove_static_field(id: int) -> None: ...
