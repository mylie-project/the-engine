package mylie.engine.core;

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
}
