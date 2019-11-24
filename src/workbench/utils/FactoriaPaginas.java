package workbench.utils;

import com.dlsc.workbenchfx.util.WorkbenchUtils;
import com.dlsc.workbenchfx.view.controls.module.Tile;

import application.Main;
import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.GridPane;



public class FactoriaPaginas extends SkinBase<Pagina> {

  private final ObservableList<Tile> tiles;
  private GridPane tilePane;

  public FactoriaPaginas(Pagina page) {
    super(page);
    tiles = page.getTiles();

    initializeParts();

    setupSkin(); // initial setup
    setupListeners(); // setup for changing modules

    getChildren().add(tilePane);
  }

  private void initializeParts() {
    tilePane = new GridPane();
    tilePane.getStyleClass().add("tile-pane");
  }

  private void setupListeners() {
    tiles.addListener((InvalidationListener) observable -> setupSkin());
  }

  private void setupSkin() {
		
    // remove any pre-existing tiles
    tilePane.getChildren().clear();

    int column = 0;
    int row = 0;

    final int columnsPerRow = WorkbenchUtils.calculateColumnsPerRow(tiles.size());
    for (Tile tile : tiles) {
      tilePane.add(tile, column, row);
      column++;

      if (column == columnsPerRow) {
        column = 0;
        row++;
      }
    }
  }

}
