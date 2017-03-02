package com.farshidabz.coordinatorbehavior;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by FarshidAbz.
 * Since 3/2/2017.
 */

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
