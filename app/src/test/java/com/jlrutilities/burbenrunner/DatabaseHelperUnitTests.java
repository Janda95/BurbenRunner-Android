package com.jlrutilities.burbenrunner;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(RobolectricTestRunner.class)
public class DatabaseHelperUnitTests {
  private RouteDatabaseHelper databaseHelper;

  @Before
  public void createDb(){
    Context context = ApplicationProvider.getApplicationContext();

    databaseHelper = new RouteDatabaseHelper(context);
  }

  @After
  public void closeDb() throws IOException {
    databaseHelper.close();
  }



  @Test
  public void testDatabaseOpen() {
    assertTrue(databaseHelper.isOpen());
  }

  @Test
  public void addition_isCorrect() {
    assertEquals(4, 2 + 2);
  }
}