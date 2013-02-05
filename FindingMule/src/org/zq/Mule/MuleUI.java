/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zq.Mule;

import javax.swing.JPanel;
import org.gephi.statistics.spi.Statistics;
import org.gephi.statistics.spi.StatisticsUI;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author zqlee1
 */
@ServiceProvider(service = StatisticsUI.class)
public class MuleUI implements StatisticsUI{

    private static final String MULE_UI_NAME = "Redundancy";
    private static final String MULE_DESC = "Reduces the graph based on subsumption relationship";

    JPanel mulePanel;
    GetMule redundStats;

    @Override
    public JPanel getSettingsPanel() {
        if(mulePanel == null){
            mulePanel = new MulePanel();
        }

        return mulePanel;
    }

    @Override
    public void setup(Statistics ststcs) {
        // dont need to pass in any parameter
        this.redundStats = (GetMule) ststcs;
    }

    @Override
    public void unsetup() {
        // dont need to output any parameter
    }

    @Override
    public Class<? extends Statistics> getStatisticsClass() {
        return GetMule.class;
    }

    @Override
    public String getValue() {
        return String.format("%.2f Iter: %d", redundStats.getRedundancyRatio(), redundStats.getIterCount());
    }

    @Override
    public String getDisplayName() {
        return MULE_UI_NAME;
    }

    @Override
    public String getShortDescription() {
        return MULE_DESC;
    }

    @Override
    public String getCategory() {
        return StatisticsUI.CATEGORY_NETWORK_OVERVIEW;
    }

    @Override
    public int getPosition() {
        return 11000; //copied from example
    }

}
