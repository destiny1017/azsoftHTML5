<!DOCTYPE html>
<html>
<head>
  <meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
  <title>tree Context menu</title>

  <link type="text/css" rel="stylesheet" href="https://ajax.googleapis.com/ajax/libs/jqueryui/1/themes/start/jquery-ui.css" />
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/1/jquery.min.js" type="text/javascript"></script>
  <script src="https://ajax.googleapis.com/ajax/libs/jqueryui/1/jquery-ui.min.js" type="text/javascript"></script>

  <link href="../../lib/node_modules/fancytree/src/skin-lion/ui.fancytree.css" rel="stylesheet" type="text/css">
  <script src="../../lib/node_modules/fancytree/src/jquery.fancytree.js" type="text/javascript"></script>

  <!-- jquery-contextmenu (https://github.com/mar10/jquery-ui-contextmenu/) -->
  <script src="https://wwwendt.de/tech/demo/jquery-contextmenu/jquery.ui-contextmenu.js" type="text/javascript"></script>

  <!-- (Irrelevant source removed.) -->

<style type="text/css">
  .ui-menu {
    width: 100px;
    font-size: 63%;
  }
</style>

<!-- Add code to initialize the tree when the document is loaded: -->
  <script type="text/javascript">
  $(function() {
    $("#tree").fancytree({
//            extensions: ['dnd'],
      source: {
        url: "ajax-tree-plain.json"
      }
    });

    $("#tree").contextmenu({
      delegate: "span.fancytree-title",
//      menu: "#options",
      menu: [
          {title: "Cut", cmd: "cut", uiIcon: "ui-icon-scissors"},
          {title: "Copy", cmd: "copy", uiIcon: "ui-icon-copy"},
          {title: "Paste", cmd: "paste", uiIcon: "ui-icon-clipboard", disabled: false },
          {title: "----"},
          {title: "Edit", cmd: "edit", uiIcon: "ui-icon-pencil", disabled: true },
          {title: "Delete", cmd: "delete", uiIcon: "ui-icon-trash", disabled: true },
          {title: "More", children: [
            {title: "Sub 1", cmd: "sub1"},
            {title: "Sub 2", cmd: "sub1"}
            ]}
          ],
      beforeOpen: function(event, ui) {
        var node = $.ui.fancytree.getNode(ui.target);
//                node.setFocus();
        node.setActive();
      },
      select: function(event, ui) {
        var node = $.ui.fancytree.getNode(ui.target);
        alert("select " + ui.cmd + " on " + node);
      }
    });
  });
  </script>
</head>

<body class="example_tree">

<!-- Tree wrapper -->
<div id="tree"></div>

</body>
</html>
