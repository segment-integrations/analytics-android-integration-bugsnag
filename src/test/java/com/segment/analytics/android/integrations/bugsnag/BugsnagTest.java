package com.segment.analytics.android.integrations.bugsnag;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import com.bugsnag.android.Bugsnag;
import com.segment.analytics.Analytics;
import com.segment.analytics.Traits;
import com.segment.analytics.ValueMap;
import com.segment.analytics.core.tests.BuildConfig;
import com.segment.analytics.test.IdentifyPayloadBuilder;
import com.segment.analytics.test.ScreenPayloadBuilder;
import com.segment.analytics.test.TrackPayloadBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static com.segment.analytics.Utils.createTraits;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18, manifest = Config.NONE)
@PowerMockIgnore({ "org.mockito.*", "org.robolectric.*", "android.*" })
@PrepareForTest(Bugsnag.class)
public class BugsnagTest {
  @Rule public PowerMockRule rule = new PowerMockRule();
  @Mock Application context;
  @Mock Analytics analytics;
  BugsnagIntegration integration;

  @Before public void setUp() {
    initMocks(this);
    PowerMockito.mockStatic(Bugsnag.class);
    when(analytics.getApplication()).thenReturn(context);

    integration = new BugsnagIntegration(analytics, new ValueMap().putValue("apiKey", "foo"));
    // Twice so we can initialize mocks for tests, but reset the mock after initialization.
    PowerMockito.mockStatic(Bugsnag.class);
  }

  @Test public void initialize() throws IllegalStateException {
    integration = new BugsnagIntegration(analytics, new ValueMap().putValue("apiKey", "foo"));
    verifyStatic();
    Bugsnag.init(context, "foo");
  }

  @Test public void activityCreate() {
    Activity activity = mock(Activity.class);
    when(activity.getLocalClassName()).thenReturn("foo");
    Bundle bundle = mock(Bundle.class);
    integration.onActivityCreated(activity, bundle);
    verifyStatic();
    Bugsnag.setContext("foo");
  }

  @Test public void identify() {
    Traits traits = createTraits("foo").putEmail("bar").putName("baz");
    integration.identify(new IdentifyPayloadBuilder().traits(traits).build());
    verifyStatic();
    Bugsnag.setUser("foo", "bar", "baz");
    verifyStatic();
    Bugsnag.addToTab("User", "userId", "foo");
    verifyStatic();
    Bugsnag.addToTab("User", "email", "bar");
    verifyStatic();
    Bugsnag.addToTab("User", "name", "baz");
  }

  @Test public void track() {
    integration.track(new TrackPayloadBuilder().event("foo").build());
    verifyStatic();
    Bugsnag.leaveBreadcrumb("foo");
  }

  @Test public void screen() {
    integration.screen(new ScreenPayloadBuilder().name("foo").build());
    verifyStatic();
    Bugsnag.leaveBreadcrumb("Viewed foo Screen");
  }
}