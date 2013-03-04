/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zq.BrandesBetw;

import javax.swing.JPanel;
import org.gephi.statistics.spi.Statistics;
import org.gephi.statistics.spi.StatisticsUI;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author zqlee1
 */
@ServiceProvider(service = StatisticsUI.class)
public class BrandesUI implements StatisticsUI{

    Brandes brandes;

    @Override
    public JPanel getSettingsPanel() {
        return null;
    }

    @Override
    public void setup(Statistics ststcs) {
        this.brandes = (Brandes)ststcs;
    }

    @Override
    public void unsetup() {
        //do nothing
    }

    @Override
    public Class<? extends Statistics> getStatisticsClass() {
        return Brandes.class;
    }

    @Override
    public String getValue() {
        return ""; //no value to display
    }

    @Override
    public String getDisplayName() {
        return "Brandes Betweenness Centrality";
    }

    @Override
    public String getShortDescription() {
        return "Computes betweenness centrality based on Brandes' algorithm";
    }

    @Override
    public String getCategory() {
        return StatisticsUI.CATEGORY_NETWORK_OVERVIEW;
    }

    @Override
    public int getPosition() {
        return 11000;
    }

}
