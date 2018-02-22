package Client.gui;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class FontHandler {

    public static void setTitleFont(Node n, Double scaleFactor) {
        if(n instanceof Label) {
            Label temp = (Label) n;
            Font cf = temp.getFont();
            temp.setFont(Font.font(cf.getFamily(), FontWeight.BOLD, cf.getSize() * scaleFactor));
        }
    }
}
