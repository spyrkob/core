package org.jboss.as.console.client.shared.subsys.web;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.runtime.RuntimeBaseAddress;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.web.model.HttpConnector;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;
import org.jboss.dmr.client.dispatch.AsyncCommand;
import org.jboss.dmr.client.dispatch.DispatchAsync;
import org.jboss.dmr.client.dispatch.impl.DMRAction;
import org.jboss.dmr.client.dispatch.impl.DMRResponse;

import java.util.ArrayList;
import java.util.List;

import static org.jboss.dmr.client.ModelDescriptionConstants.*;

/**
 * @author Heiko Braun
 * @date 12/10/11
 */
public class LoadConnectorCmd implements AsyncCommand<List<HttpConnector>>{

    private final boolean runtime;
    private DispatchAsync dispatcher;
    private BeanFactory factory;

    public LoadConnectorCmd(DispatchAsync dispatcher, BeanFactory beanFactory, boolean runtime) {
        this.dispatcher = dispatcher;
        this.factory= beanFactory;
        this.runtime = runtime;
    }

    @Override
    public void execute(final AsyncCallback<List<HttpConnector>> callback) {

        ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);

        if(runtime)
            operation.get(ADDRESS).set(RuntimeBaseAddress.get());
        else
            operation.get(ADDRESS).set(Baseadress.get());

        operation.get(ADDRESS).add("subsystem", "web");
        operation.get(CHILD_TYPE).set("connector");
        operation.get(RECURSIVE).set(Boolean.TRUE);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();

                if(response.isFailure())
                {
                    callback.onFailure(new RuntimeException("Failed to load connectors: "+response.getFailureDescription()));
                }
                else
                {


                    List<Property> propList = response.get(RESULT).asPropertyList();
                    List<HttpConnector> connectors = new ArrayList<HttpConnector>(propList.size());

                    for(Property prop : propList)
                    {
                        String name = prop.getName();
                        ModelNode propValue = prop.getValue();

                        HttpConnector connector = factory.httpConnector().as();
                        connector.setName(name);

                        // TODO: https://issues.jboss.org/browse/AS7-747
                        if(propValue.hasDefined("enabled"))
                            connector.setEnabled(propValue.get("enabled").asBoolean());
                        else
                            connector.setEnabled(true); // the default value

                        connector.setScheme(propValue.get("scheme").asString());
                        connector.setSocketBinding(propValue.get("socket-binding").asString());
                        connector.setProtocol(propValue.get("protocol").asString());

                        connectors.add(connector);
                    }

                    callback.onSuccess(connectors);

                }


            }
        });
    }
}