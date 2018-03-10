# CoordinatorBehavior

In this example I've tried to replicate the profile animation of Telegram to show how a `CoordinatorLayout.Behavior` could be used.

### Demo

![](https://github.com/FarshidABZ/CoordinatorBehavior/blob/master/art/profile2.gif?raw=true =250x250)

### Usage

## Behavior

  To custom the behavior of these elements, the first work is make a subclass of CoordinatorLayout.Behavior<T>,
  been T is your child class, for example: ImageView. These are methods we must override:
  
  - layoutDependsOn(): called every time that something happens in the layout,
    what we must do to return true once we identify the dependency, 
    in the example, this method is automatically fired when the user scrolls (because the Toolbar will move),
    in that way we can make our child sight react accordingly. 
    
  - onDependentViewChanged(): Called when layoutDependsOn() return true.
    Here is where you must to implement our animations, 
    translations or movements always related with the provided dependency.
    
    Calculating the Toolbar height to placing the child view

``` java
public class ImageBehavior extends CoordinatorLayout.Behavior {
    private final Context context;

    private int startXPosition;
    private float startToolbarPosition;

    private int startYPosition;

    private int finalYPosition;
    private int finalHeight;
    private int startHeight;
    private int finalXPosition;

    public ImageBehavior(Context context, AttributeSet attrs) {
        this.context = context;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof Toolbar;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        maybeInitProperties((ImageView) child, dependency);

        final int maxScrollDistance = (int) (startToolbarPosition - getStatusBarHeight());
        float expandedPercentageFactor = dependency.getY() / maxScrollDistance;

        float distanceYToSubtract = ((startYPosition - finalYPosition)
                * (1f - expandedPercentageFactor)) + (child.getHeight() / 2);

        float distanceXToSubtract = ((startXPosition - finalXPosition)
                * (1f - expandedPercentageFactor)) + (child.getWidth() / 2);

        float heightToSubtract = ((startHeight - finalHeight) * (1f - expandedPercentageFactor));

        child.setY(startYPosition - distanceYToSubtract);
        child.setX(startXPosition - distanceXToSubtract);

        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        lp.width = (int) (startHeight - heightToSubtract);
        lp.height = (int) (startHeight - heightToSubtract);
        child.setLayoutParams(lp);
        return true;
    }

    @SuppressLint("PrivateResource")
    private void maybeInitProperties(ImageView child, View dependency) {
        if (startYPosition == 0)
            startYPosition = (int) (dependency.getY());

        if (finalYPosition == 0)
            finalYPosition = (dependency.getHeight() / 2);

        if (startHeight == 0)
            startHeight = child.getHeight();

        if (finalHeight == 0)
            finalHeight = context.getResources().getDimensionPixelOffset(R.dimen.image_small_width);

        if (startXPosition == 0)
            startXPosition = (int) (child.getX() + (child.getWidth() / 2));

        if (finalXPosition == 0)
            finalXPosition = context.getResources().getDimensionPixelOffset(R.dimen.abc_action_bar_content_inset_material) + (finalHeight / 2);

        if (startToolbarPosition == 0)
            startToolbarPosition = dependency.getY() + (dependency.getHeight() / 2);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");

        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}

```

## UI

The next step is creating an Activity to test this custom Behavior. 
Declaring it's layout with CoordinatorLayout as root, AppBarLayout and CollapsingToolbarLayout: 

```xml
<?xml version="1.0" encoding="utf-8"?>
<android.support.design.widget.CoordinatorLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/activity_main"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#FFFFFF">

    <android.support.design.widget.AppBarLayout
        android:id="@+id/appbar"
        android:layout_width="match_parent"
        android:layout_height="256dp">

        <android.support.design.widget.CollapsingToolbarLayout
            android:id="@+id/collapsing"
            android:layout_width="match_parent"
            android:layout_height="256dp"
            app:layout_scrollFlags="scroll|exitUntilCollapsed|snap">

            <FrameLayout
                android:id="@+id/flTitle"
                android:layout_width="match_parent"
                android:layout_height="100dp"
                android:layout_gravity="bottom|center_horizontal"
                android:background="#FFFFFF"
                android:orientation="vertical"
                app:layout_collapseMode="parallax">

                <LinearLayout
                    android:id="@+id/llTitle"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_gravity="center"
                    android:gravity="center"
                    android:orientation="vertical">

                    <TextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="Header"
                        android:textColor="@android:color/black"
                        android:textSize="25sp" />

                    <TextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:layout_marginTop="4dp"
                        android:text="Title"
                        android:textColor="@android:color/black" />

                </LinearLayout>
            </FrameLayout>
        </android.support.design.widget.CollapsingToolbarLayout>

    </android.support.design.widget.AppBarLayout>


    <android.support.v4.widget.NestedScrollView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_marginTop="@dimen/activity_horizontal_margin"
        android:background="#FFFFFF"
        android:scrollbars="none"
        app:behavior_overlapTop="0.1dp"
        app:layout_behavior="@string/appbar_scrolling_view_behavior">

        <android.support.v7.widget.CardView
            android:layout_width="match_parent"
            android:layout_height="match_parent"/>

    </android.support.v4.widget.NestedScrollView>

    <android.support.v7.widget.Toolbar
        android:id="@+id/toolbar"
        android:layout_width="match_parent"
        android:layout_height="?attr/actionBarSize"
        android:background="#FFFFFF"
        app:layout_anchor="@id/flTitle">

        <LinearLayout
            android:layout_width="wrap_content"
            android:layout_height="match_parent"
            android:orientation="horizontal">

            <TextView
                android:id="@+id/tvToolbarTitle"
                android:layout_width="wrap_content"
                android:layout_height="match_parent"
                android:layout_marginLeft="56dp"
                android:gravity="center_vertical"
                android:text="Toolbar Title"
                android:textColor="@android:color/black"
                android:textSize="20sp" />

        </LinearLayout>
    </android.support.v7.widget.Toolbar>

    <ImageView
        android:id="@+id/imgAvatar"
        android:layout_width="64dp"
        android:layout_height="64dp"
        android:layout_gravity="center"
        android:src="@color/colorAccent"
        app:layout_behavior="com.farshidabz.coordinatorbehavior.ImageBehavior" />

</android.support.design.widget.CoordinatorLayout>

```

## Control the view

Create a class to control and handle appbar behavior:

``` java
class HandleProfileViewBehavior implements AppBarLayout.OnOffsetChangedListener {

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.7f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.7f;
    private static final int ALPHA_ANIMATIONS_DURATION = 250;

    private boolean isTheTitleVisible = false;
    private boolean isTheTitleContainerVisible = true;

    private LinearLayout linearLayoutTitle;
    private TextView textViewTitle;

    HandleProfileViewBehavior(View rootView) {

        AppBarLayout appbar = (AppBarLayout) rootView.findViewById(R.id.appbar);
        linearLayoutTitle = (LinearLayout) rootView.findViewById(R.id.llTitle);
        textViewTitle = (TextView) rootView.findViewById(R.id.tvToolbarTitle);

        appbar.addOnOffsetChangedListener(this);

        startAlphaAnimation(textViewTitle, 0, View.INVISIBLE);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if (!isTheTitleVisible) {
                startAlphaAnimation(textViewTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                isTheTitleVisible = true;
            }
        } else {
            if (isTheTitleVisible) {
                startAlphaAnimation(textViewTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                isTheTitleVisible = false;
            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if (isTheTitleContainerVisible) {
                startAlphaAnimation(linearLayoutTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                isTheTitleContainerVisible = false;
            }
        } else {

            if (!isTheTitleContainerVisible) {
                startAlphaAnimation(linearLayoutTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                isTheTitleContainerVisible = true;
            }
        }
    }

    private static void startAlphaAnimation(View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }
}

```


And finally in your Activity create an instance of HandleProfileViewBehavior.class like this:

``` java

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        HandleProfileViewBehavior handleProfileViewBehavior =
                new HandleProfileViewBehavior(findViewById(R.id.activity_main));
    }
}

```


