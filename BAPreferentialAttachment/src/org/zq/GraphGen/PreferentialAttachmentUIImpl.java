/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zq.GraphGen;

import javax.swing.JPanel;
import org.gephi.io.generator.spi.Generator;
import org.gephi.io.generator.spi.GeneratorUI;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author zqlee1
 */
@ServiceProvider(service = PreferentialAttachmentUI.class)
public class PreferentialAttachmentUIImpl implements PreferentialAttachmentUI{

    private PreferentialAttachmentPanel panel;
    private PreferentialAttachment paGen;

    @Override
    public JPanel getPanel() {
        if(panel == null){
            panel = new PreferentialAttachmentPanel();
        }
        return PreferentialAttachmentPanel.createValidationPanel(panel);
    }

    // show the panel
    @Override
    public void setup(Generator gnrtr) {
        this.paGen = (PreferentialAttachment) gnrtr;

        if(panel == null){
            panel = new PreferentialAttachmentPanel();
        }

        panel.initNodeField.setText(String.valueOf(paGen.getNumInitNodes()));
        panel.nodeField.setText(String.valueOf(paGen.getNumNodes()));
        panel.edgeField.setText(String.valueOf(paGen.getNumEdges()));
    }

    //gets input from panel into the generator
    @Override
    public void unsetup() {
        paGen.setNumInitNodes(Integer.parseInt(panel.initNodeField.getText()));
        paGen.setNumNodes(Integer.parseInt(panel.nodeField.getText()));
        paGen.setNumEdges(Integer.parseInt(panel.edgeField.getText()));
        panel = null;
    }

}
