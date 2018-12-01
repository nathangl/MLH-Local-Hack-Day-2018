package com.google.ar.sceneform.samples.hellosceneform;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalItem;
import com.esri.arcgisruntime.security.AuthenticationManager;
import com.esri.arcgisruntime.security.Credential;
import com.esri.arcgisruntime.security.DefaultAuthenticationChallengeHandler;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;

public class Assignments {
    ServiceFeatureTable mServiceFeatureTable;
    FeatureLayer mFeaturelayer;
    Location location;

    private String assignmentsId;
    private Portal portal;

    public Assignments(Activity mainActivity) {

        try{
        assignmentsId = "d4523d7fd0414d9f939e99474c0285e6";
        // Set the DefaultAuthenticationChallegeHandler to allow authentication with the portal.
        DefaultAuthenticationChallengeHandler handler = new DefaultAuthenticationChallengeHandler(mainActivity);
        AuthenticationManager.setAuthenticationChallengeHandler(handler);
        portal = new Portal("https://esri.qservco.com/portal/", true);

        final PortalItem portalItem = new PortalItem(portal, assignmentsId);

        // create feature layer with its service feature table
        // create the service feature table
        //mServiceFeatureTable = new ServiceFeatureTable("https://esri.qservco.com/server/rest/services/Hosted/assignments_acdd665220fc4bcf925cfe78e24bd6a4/FeatureServer");
        // create the feature layer using the service feature table
        mFeaturelayer = new FeatureLayer(portalItem,0);}
        catch(Exception ex){
            Log.e("ARGIS", ex.getMessage());
        }
    }

    public void setLocation(Location location){
        this.location = location;
    }

    public void searchForState() {

        // clear any previous selections
        mFeaturelayer.clearSelection();

        // create objects required to do a selection with a query
        QueryParameters query = new QueryParameters();
        //make search case insensitive

        //sdo_geom.sdo_buffer(MDSYS.SDO_GEOMETRY(2001,8307,MDSYS.SDO_POINT_TYPE("+location.getLongitude() + ","+location.getLatitude()+",NULL),NULL,NULL), 10,.005, 'units=foot')

        query.setWhereClause("sdo_anyinteract(shape, sdo_geom.sdo_buffer(MDSYS.SDO_GEOMETRY(2001,8307,MDSYS.SDO_POINT_TYPE("+location.getLongitude() + ","+location.getLatitude()+",NULL),NULL,NULL), 10,.005, 'units=foot')) = 'TRUE'");

        // call select features
        final ListenableFuture<FeatureQueryResult> future = mServiceFeatureTable.queryFeaturesAsync(query);
        // add done loading listener to fire when the selection returns
        future.addDoneListener(new Runnable() {
            @Override
            public void run() {
                try {
                    // call get on the future to get the result
                    FeatureQueryResult result = future.get();

                    // check there are some results
                    if (result.iterator().hasNext()) {

                        // get the extend of the first feature in the result to zoom to
                        Feature feature = result.iterator().next();
                        Geometry geom = feature.getGeometry();

                        Log.d("ARGIS","Found Geom");

                    } else {
                        /*Toast.makeText(MainActivity.this, "No states found with name: " + searchString, Toast.LENGTH_SHORT).show();*/
                    }
                } catch (Exception e) {
                    /*Toast.makeText(MainActivity.this, "Feature search failed for: " + searchString + ". Error=" + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    Log.e(getResources().getString(R.string.app_name),
                            "Feature search failed for: " + searchString + ". Error=" + e.getMessage());*/
                }
            }
        });
    }
}
