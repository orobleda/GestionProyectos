package workbench.utils;

import com.dlsc.workbenchfx.Workbench;
import com.dlsc.workbenchfx.view.controls.module.Tab;
import javafx.scene.control.Skin;

public class Pestania extends Tab {

  public Pestania(Workbench workbench) {
    super(workbench);
  }

  @Override
  protected Skin<?> createDefaultSkin() {
    return new FactoriaPestanias(this);
  }
}
