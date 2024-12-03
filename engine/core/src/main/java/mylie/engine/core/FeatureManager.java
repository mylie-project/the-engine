package mylie.engine.core;

import mylie.engine.core.features.async.*;
import mylie.util.configuration.Configuration;

import java.util.ArrayList;
import java.util.List;

public class FeatureManager {
    private final Configuration<Engine> engineConfiguration;
    private final List<Feature> featureList;

    public FeatureManager(Configuration<Engine> engineConfiguration) {
        this.engineConfiguration = engineConfiguration;
        featureList=new ArrayList<>();
    }

    public <F extends Feature> void add(F feature) {
        featureList.add(feature);
        if(feature instanceof Feature.Engine engineFeature) {
            engineFeature.onSetup(this,engineConfiguration);
        }
    }

    public <F extends Feature> F get(Class<F> schedulerClass) {
        for (Feature feature : featureList) {
            if(schedulerClass.isAssignableFrom(feature.getClass())){
                return schedulerClass.cast(feature);
            }
        }
        return null;
    }

    public void onUpdate() {
        Async.await(
        Async.async(Async.Mode.Async, Cache.OneFrame,Async.BACKGROUND,0,featureList,Feature.Lifecycle.Update.class,updateFunction)
        );
        //for (Feature feature : featureList) {
        //    if(feature instanceof Feature.Lifecycle.Update updateFeature) {
        //        updateFeature.onUpdate();
        //    }
        //}
    }

    private static Functions.F0<Boolean,Feature.Lifecycle.Update> updateFunction = new Functions.F0<>("FeatureUpdateFunction") {
        @Override
        protected Boolean run(Feature.Lifecycle.Update o) {
            o.onUpdate();
            return true;
        }
    };
}
