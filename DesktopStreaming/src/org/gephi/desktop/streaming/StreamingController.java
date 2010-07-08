/*
Copyright 2008 WebAtlas
Authors : Mathieu Bastian, Mathieu Jacomy, Julian Bilcke
Website : http://www.gephi.org

This file is part of Gephi.

Gephi is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Gephi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gephi.desktop.streaming;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.project.api.Project;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.project.api.WorkspaceInformation;
import org.gephi.project.api.WorkspaceListener;
import org.gephi.streaming.api.DefaultGraphStreamingEventProcessor;
import org.gephi.streaming.api.GraphStreamingEndpoint;
import org.gephi.streaming.api.StreamingConnection;
import org.gephi.streaming.server.ServerController;
import org.gephi.streaming.server.StreamingServer;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author panisson
 *
 */
@ServiceProvider(service = StreamingController.class)
public class StreamingController {
    
    private StreamingModel model;
    private StreamingServerPanel serverPanel;
    
    public StreamingController() {
      //Workspace events
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.addWorkspaceListener(new WorkspaceListener() {

            public void initialize(Workspace workspace) {
                model = new StreamingModel();
                workspace.add(model);
            }

            public void select(Workspace workspace) {
                model = workspace.getLookup().lookup(StreamingModel.class);
                if (model == null) {
                    model = new StreamingModel();
                    workspace.add(model);
                }
                refreshModel();
            }

            public void unselect(Workspace workspace) {
            }

            public void close(Workspace workspace) {
            }

            public void disable() {
                model = null;
            }
        });

        if (pc.getCurrentWorkspace() != null) {
            model = pc.getCurrentWorkspace().getLookup().lookup(StreamingModel.class);
            if (model == null) {
                model = new StreamingModel();
                pc.getCurrentWorkspace().add(model);
            }
        }
    }
    
    public void setServerPanel(StreamingServerPanel panel) {
        this.serverPanel = panel;
    }

    public void refreshModel() {
        if (serverPanel!=null) {
            serverPanel.refreshModel();
        }
    }
    
    public StreamingModel getStreamingModel() {
        return model;
    }
    
    public void connectToStream(GraphStreamingEndpoint endpoint) {
        ProjectController projectController = Lookup.getDefault().lookup(ProjectController.class);
        Project project = projectController.getCurrentProject();
        if (project==null)
            projectController.newProject();
        Workspace workspace = projectController.getCurrentWorkspace();
        if (workspace==null)
            workspace = projectController.newWorkspace(projectController.getCurrentProject());
//        projectController.openWorkspace(workspace);

        GraphController graphController = Lookup.getDefault().lookup(GraphController.class);
        GraphModel graphModel = graphController.getModel();

        DefaultGraphStreamingEventProcessor eventProcessor = 
            new DefaultGraphStreamingEventProcessor(graphModel.getHierarchicalMixedGraph());
        StreamingConnection connection = eventProcessor.process(endpoint);
        
        model.getActiveConnections().add(connection);
     }
    
    public void disconnect(StreamingConnection connection) {
        try {
            connection.close();
            model.getActiveConnections().remove(connection);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void exposeWorkspaceAsStream() {
        ProjectController projectController = Lookup.getDefault().lookup(ProjectController.class);
        Project project = projectController.getCurrentProject();
        if (project==null) {
            //TODO: Invalid project
            return;
        }
        
        Workspace workspace = projectController.getCurrentWorkspace();
        if (workspace==null) {
            //TODO: Invalid workspace
            return;
        }
        
//        GraphController graphController = workspace.getLookup().lookup(GraphController.class);
        GraphController graphController = Lookup.getDefault().lookup(GraphController.class);
        Graph graph = graphController.getModel().getMixedGraph();

        WorkspaceInformation wi = workspace.getLookup().lookup(WorkspaceInformation.class);
        String context = "/"+wi.getName().replaceAll(" ", "").toLowerCase();
        
        StreamingServer server = Lookup.getDefault().lookup(StreamingServer.class);
        ServerController serverController = new ServerController(graph);

        server.register(serverController, context);

        try {
            server.start();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        model.setServerContext(context);
        model.setServerRunning(true);
    }
    
    public void stopServer() {
        model.setServerRunning(false);
        StreamingServer server = Lookup.getDefault().lookup(StreamingServer.class);
        server.unregister(model.getServerContext());
    }
}
