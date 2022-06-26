package com.androidfabric;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.androidfabric.newarchitecture.components.ColoredViewManager;
import com.androidfabric.newarchitecture.MainApplicationReactNativeHost;
import com.androidfabric.newarchitecture.components.MainComponentsRegistry;
import com.facebook.react.PackageList;
import com.facebook.react.ReactApplication;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.TurboReactPackage;
import com.facebook.react.bridge.JSIModulePackage;
import com.facebook.react.bridge.JSIModuleProvider;
import com.facebook.react.bridge.JSIModuleSpec;
import com.facebook.react.bridge.JSIModuleType;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.UIManager;
import com.facebook.react.config.ReactFeatureFlags;
import com.facebook.react.fabric.ComponentFactory;
import com.facebook.react.fabric.CoreComponentsRegistry;
import com.facebook.react.fabric.FabricJSIModuleProvider;
import com.facebook.react.fabric.ReactNativeConfig;
import com.facebook.react.module.model.ReactModuleInfoProvider;
import com.facebook.react.uimanager.ViewManager;
import com.facebook.react.uimanager.ViewManagerRegistry;
import com.facebook.soloader.SoLoader;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class MainApplication extends Application implements ReactApplication {

    private final ReactNativeHost mReactNativeHost =
            new ReactNativeHost(this) {
                @Override
                public boolean getUseDeveloperSupport() {
                    return BuildConfig.DEBUG;
                }

                @Nullable
                @Override
                protected JSIModulePackage getJSIModulePackage() {
                    return (reactApplicationContext, jsContext) -> {
                        final List<JSIModuleSpec> specs = new ArrayList<>();
                        specs.add(new JSIModuleSpec() {
                            @Override
                            public JSIModuleType getJSIModuleType() {
                                return JSIModuleType.UIManager;
                            }

                            @Override
                            public JSIModuleProvider<UIManager> getJSIModuleProvider() {
                                final ComponentFactory componentFactory = new ComponentFactory();
                                CoreComponentsRegistry.register(componentFactory);
                                MainComponentsRegistry.register(componentFactory);
                                final ReactInstanceManager reactInstanceManager = getReactInstanceManager();

                                ViewManagerRegistry viewManagerRegistry =
                                        new ViewManagerRegistry(
                                                reactInstanceManager.getOrCreateViewManagers(
                                                        reactApplicationContext));

                                return new FabricJSIModuleProvider(
                                        reactApplicationContext,
                                        componentFactory,
                                        ReactNativeConfig.DEFAULT_CONFIG,
                                        viewManagerRegistry);
                            }
                        });
                        return specs;
                    };

                }

                @Override
                protected List<ReactPackage> getPackages() {
                    @SuppressWarnings("UnnecessaryLocalVariable")
                    List<ReactPackage> packages = new PackageList(this).getPackages();
                    // Packages that cannot be autolinked yet can be added manually here, for example:
                    // packages.add(new MyReactNativePackage());
                    packages.add(new ReactPackage() {

                        @NonNull
                        @Override
                        public List<NativeModule> createNativeModules(@NonNull ReactApplicationContext reactContext) {
                            return Collections.emptyList();
                        }

                        @Override
                        public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
                            List<ViewManager> viewManagers = new ArrayList<>();
                            viewManagers.add(new ColoredViewManager(reactContext));
                            return viewManagers;
                        }
                    });
                    return packages;
                }

                @Override
                protected String getJSMainModuleName() {
                    return "index";
                }
            };

    private final ReactNativeHost mNewArchitectureNativeHost =
            new MainApplicationReactNativeHost(this);

    @Override
    public ReactNativeHost getReactNativeHost() {
        if (BuildConfig.IS_NEW_ARCHITECTURE_ENABLED) {
            return mNewArchitectureNativeHost;
        } else {
            return mReactNativeHost;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // If you opted-in for the New Architecture, we enable the TurboModule system
        ReactFeatureFlags.useTurboModules = BuildConfig.IS_NEW_ARCHITECTURE_ENABLED;
        SoLoader.init(this, /* native exopackage */ false);
        initializeFlipper(this, getReactNativeHost().getReactInstanceManager());
    }

    /**
     * Loads Flipper in React Native templates. Call this in the onCreate method with something like
     * initializeFlipper(this, getReactNativeHost().getReactInstanceManager());
     *
     * @param context
     * @param reactInstanceManager
     */
    private static void initializeFlipper(
            Context context, ReactInstanceManager reactInstanceManager) {
        if (BuildConfig.DEBUG) {
            try {
        /*
         We use reflection here to pick up the class that initializes Flipper,
        since Flipper library is not available in release mode
        */
                Class<?> aClass = Class.forName("com.androidfabric.ReactNativeFlipper");
                aClass
                        .getMethod("initializeFlipper", Context.class, ReactInstanceManager.class)
                        .invoke(null, context, reactInstanceManager);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
