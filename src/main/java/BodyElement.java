import javax.swing.*;
import java.awt.*;

public class BodyElement extends JPanel {


    public BodyElement() {
        this.setSize(GameFrame.BODY_SIZE, GameFrame.BODY_SIZE);
        this.setBackground(GameFrame.tailColor);
        this.setVisible(true);
    }
}
