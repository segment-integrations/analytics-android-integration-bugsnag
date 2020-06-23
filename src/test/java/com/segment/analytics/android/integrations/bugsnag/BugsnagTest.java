package com.segment.analytics.android.integrations.bugsnag;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.bugsnag.android.Client;
import com.segment.analytics.Analytics;
import com.segment.analytics.Traits;
import com.segment.analytics.ValueMap;
import com.segment.analytics.core.tests.BuildConfig;
import com.segment.analytics.test.IdentifyPayloadBuilder;
import com.segment.analytics.test.ScreenPayloadBuilder;
import com.segment.analytics.test.TrackPayloadBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.segment.analytics.Utils.createTraits;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18, manifest = Config.NONE)
public class BugsnagTest {
  @Mock Application context;
  @Mock Analytics analytics;
  @Mock Client client;
  BugsnagIntegration integration;

  @Before public void setUp() {
    initMocks(this);
    BugsnagIntegration.Provider provider = new BugsnagIntegration.Provider() {
      @Override public Client get(Context context, String apiKey) {
        return client;
      }
    };
    when(analytics.getApplication()).thenReturn(context);

    integration =
        new BugsnagIntegration(provider, analytics, new ValueMap().putValue("apiKey", "foo"));
  }

  @Test public void initialize() throws IllegalStateException {
    BugsnagIntegration.Provider provider = mock(BugsnagIntegration.Provider.class);
    ValueMap settings = new ValueMap().putValue("apiKey", "foo");
    integration = new BugsnagIntegration(provider, analytics, settings);
    verify(provider).get(context, "foo");
  }

  @Test public void activityCreate() {
    Activity activity = mock(Activity.class);
    when(activity.getLocalClassName()).thenReturn("foo");
    Bundle bundle = mock(Bundle.class);
    integration.onActivityCreated(activity, bundle);
    verify(client).setContext("foo");
  }

  @Test public void identify() {
    Traits traits = createTraits("foo").putEmail("bar").putName("baz");
    integration.identify(new IdentifyPayloadBuilder().traits(traits).build());
    verify(client).setUser("foo", "bar", "baz");
    verify(client).addMetadata("User", "userId", "foo");
    verify(client).addMetadata("User", "email", "bar");
    verify(client).addMetadata("User", "name", "baz");
  }

  @Test public void track() {
    integration.track(new TrackPayloadBuilder().event("foo").build());
    verify(client).leaveBreadcrumb("foo");
  }

  @Test public void screen() {
    integration.screen(new ScreenPayloadBuilder().name("foo").build());
    verify(client).leaveBreadcrumb("Viewed foo Screen");
  }
}