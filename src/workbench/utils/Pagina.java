package workbench.utils;

import com.dlsc.workbenchfx.Workbench;
import com.dlsc.workbenchfx.view.controls.module.Page;
import com.dlsc.workbenchfx.view.controls.module.Tab;
import javafx.scene.control.Skin;

public class Pagina extends Page {
  /**
   * Constructs a new {@link Tab}.
   *
   * @param workbench which created this {@link Tab}
   */
  public Pagina(Workbench workbench) {
    super(workbench);
  }

  @Override
  protected Skin<?> createDefaultSkin() {
    return new FactoriaPaginas(this);
  }
}
