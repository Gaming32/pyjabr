from typing import Any

from _java_init import FakeJavaClass


def find_class(name: str) -> int | None: ...

def remove_class(id: int) -> None: ...

def get_class_attribute(owner: FakeJavaClass, owner_id: int, name: str) -> Any: ...
