/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zq.BrandesBetw;

import org.gephi.statistics.spi.Statistics;
import org.gephi.statistics.spi.StatisticsBuilder;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author zqlee1
 */
@ServiceProvider(service = StatisticsBuilder.class)
public class BrandesBuilder implements StatisticsBuilder{
    private static final String SERVICE_NAME = "Brandes Betweenness";

    @Override
    public String getName() {
        return SERVICE_NAME;
    }

    @Override
    public Statistics getStatistics() {
        return new Brandes();
    }

    @Override
    public Class<? extends Statistics> getStatisticsClass() {
        return Brandes.class;
    }

}
