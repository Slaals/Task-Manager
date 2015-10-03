# Task-Manager

Allows to manage some projects with a progression bar and an interactive tree.

## Purposes

Basically I did this to play around with JavaFX and creating a local storage in a structured DOM in order to save a tree.

## DOM

Basically, regardless of the element depth, every data is a children of the <root> element, it avoids to manage an infinite depth that could affect read performance.

DOM structure :

```
*data* -> @id      = unique ID
          @level   = depth in the tree, first level is 0
          @project = boolean that determines if it's a folder (false) or a project (true)
          
          < name    = data name
          < parent  = parent ID, 0 if the parent is root
          
```

If a data has a project or more it cant contains another data element, only project.

## Fetching

I implemented a Depth First Search with a recursive function.

The program will get the first element, which is the first child of <root> and will search for other data or a project element.

If it encounters a data element, it will recursively search in this element, if not, it'll get the projects and pursuing the search in the last data until there is anything else to find... And so on. Basically, from up to down.
