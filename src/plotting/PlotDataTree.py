#pip3 install matplotlib pyqt5 tokenize-rt ete3
#easy_install --upgrade pip
from ete3 import Tree, faces, AttrFace, TreeStyle
import argparse

def my_layout(node):
    if node.is_leaf():
        # If terminal node, draws its name
        name_face = AttrFace("name")
    else:
        # If internal node, draws label with smaller font size
        name_face = AttrFace("name", fsize=10)
    # Adds the name face to the image at the preferred position
    faces.add_face_to_node(name_face, node, column=0, position="branch-right")


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument("--file", help="input file", type=str, required=True)
    args = parser.parse_args()
    tree_str = open(args.file).readline().strip()
    print(tree_str)
    ts = TreeStyle()
    # Do not add leaf names automatically
    ts.show_leaf_name = False
    # Use my custom layout
    ts.layout_fn = my_layout
    #ts.force_topology = True

    ts.show_scale = False
    #ts.show_branch_support = True

    t = Tree(tree_str, format=8)
    t.convert_to_ultrametric()
    t.show(tree_style=ts)
