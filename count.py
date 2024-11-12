import os
import sys
from typing import List, Optional


class FileSystem:
    def __init__(self) -> None:
        self.path: str = os.path.dirname(os.path.abspath(__file__))

    def folder_structure(self, path: Optional[str] = None) -> List[str]:
        if path is None:
            path = self.path
        structure: List[str] = []
        try:
            for root, dirs, files in os.walk(path):
                level: int = root.replace(path, "").count(os.sep)
                indent: str = " " * 4 * level
                structure.append(f"{indent}{os.path.basename(root)}/")
                subindent: str = " " * 4 * (level + 1)
                for f in files:
                    structure.append(f"{subindent}{f}")
        except OSError as e:
            print(f"Error: Unable to access directory '{path}': {e}")
        return structure

    def __str__(self) -> str:
        return f"FileSystem({self.path})"


def main() -> None:
    fs = FileSystem()
    print(f"Folder structure of current directory {fs.path}:")
    for item in fs.folder_structure():
        print(item)


if __name__ == "__main__":
    try:
        main()
    except Exception as e:
        print(f"An error occurred: {e}")
