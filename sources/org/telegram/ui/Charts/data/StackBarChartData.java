package org.telegram.ui.Charts.data;

import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.SegmentTree;

public class StackBarChartData extends ChartData {
    public long[] ySum;
    public SegmentTree ySumSegmentTree;

    public StackBarChartData(JSONObject jSONObject) throws JSONException {
        super(jSONObject);
        init();
    }

    public void init() {
        int length = this.lines.get(0).y.length;
        int size = this.lines.size();
        this.ySum = new long[length];
        for (int i = 0; i < length; i++) {
            this.ySum[i] = 0;
            for (int i2 = 0; i2 < size; i2++) {
                long[] jArr = this.ySum;
                jArr[i] = jArr[i] + this.lines.get(i2).y[i];
            }
        }
        this.ySumSegmentTree = new SegmentTree(this.ySum);
    }

    public long findMax(int i, int i2) {
        return this.ySumSegmentTree.rMaxQ(i, i2);
    }
}
