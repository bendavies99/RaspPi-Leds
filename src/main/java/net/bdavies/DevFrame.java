package net.bdavies;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;

public class DevFrame extends JFrame {

    @Getter
    private final StripPanel panel;

    public DevFrame(int numLeds, String id) throws HeadlessException {
        super("DevFrame - Preview only - " + id);
        panel = new StripPanel(numLeds);
        add(panel);
        pack();
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
