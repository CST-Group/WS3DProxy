/*****************************************************************************
 * Copyright 2007-2015 DCA-FEEC-UNICAMP
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *    Patricia Rocha de Toro, Elisa Calhau de Castro, Ricardo Ribeiro Gudwin
 *****************************************************************************/

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
