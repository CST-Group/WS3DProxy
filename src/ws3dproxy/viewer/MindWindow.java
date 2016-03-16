/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ws3dproxy.viewer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import ws3dproxy.model.Creature;
import ws3dproxy.model.World;

/**
 *
 * @author ecalhau
 */
public class MindWindow extends JFrame implements Observer {

    private static final long serialVersionUID = 7348623346461083697L;

    private JMenuBar mbar = new JMenuBar();
    private JMenu menu;
    public EnvironmentPanel envPanel;

    public MindWindow(Creature c) {
        super("Mind of robot " + c.getName());
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        envPanel = new EnvironmentPanel();
        envPanel.setCreature(c);

        menu = new JMenu("Options");
        JMenuItem exitItem;
        exitItem = new JMenuItem(" Exit");
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK));
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                System.exit(0);
                System.runFinalization();
                System.gc();
            }
        });
        menu.add(exitItem);
        mbar.add(menu);
        this.setJMenuBar(mbar);
        mbar.setVisible(true);
        getContentPane().add(envPanel);

        envPanel.addMouseListener(new MouseAdapter() {
        });

        World p = World.getInstance();
        setSize(p.getEnvironmentWidth(), p.getEnvironmentHeight());
        setVisible(true);
    }

    public void update(Observable arg0, Object arg1) {
        this.repaint();
    }
}
