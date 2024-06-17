package org.telegram.messenger.video;

import com.googlecode.mp4parser.AbstractBox;
import java.nio.ByteBuffer;
import java.util.List;
import org.telegram.messenger.video.HevcDecoderConfigurationRecord;

public class HevcConfigurationBox extends AbstractBox {
    public static final String TYPE = "hvcC";
    private HevcDecoderConfigurationRecord hevcDecoderConfigurationRecord;

    public HevcConfigurationBox() {
        super(TYPE);
        this.hevcDecoderConfigurationRecord = new HevcDecoderConfigurationRecord();
    }

    @Override
    protected long getContentSize() {
        return this.hevcDecoderConfigurationRecord.getSize();
    }

    @Override
    protected void getContent(ByteBuffer byteBuffer) {
        this.hevcDecoderConfigurationRecord.write(byteBuffer);
    }

    @Override
    protected void _parseDetails(ByteBuffer byteBuffer) {
        this.hevcDecoderConfigurationRecord.parse(byteBuffer);
    }

    public HevcDecoderConfigurationRecord getHevcDecoderConfigurationRecord() {
        return this.hevcDecoderConfigurationRecord;
    }

    public void setHevcDecoderConfigurationRecord(HevcDecoderConfigurationRecord hevcDecoderConfigurationRecord) {
        this.hevcDecoderConfigurationRecord = hevcDecoderConfigurationRecord;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        HevcDecoderConfigurationRecord hevcDecoderConfigurationRecord = this.hevcDecoderConfigurationRecord;
        HevcDecoderConfigurationRecord hevcDecoderConfigurationRecord2 = ((HevcConfigurationBox) obj).hevcDecoderConfigurationRecord;
        return hevcDecoderConfigurationRecord == null ? hevcDecoderConfigurationRecord2 == null : hevcDecoderConfigurationRecord.equals(hevcDecoderConfigurationRecord2);
    }

    public int hashCode() {
        HevcDecoderConfigurationRecord hevcDecoderConfigurationRecord = this.hevcDecoderConfigurationRecord;
        if (hevcDecoderConfigurationRecord != null) {
            return hevcDecoderConfigurationRecord.hashCode();
        }
        return 0;
    }

    public int getConfigurationVersion() {
        return this.hevcDecoderConfigurationRecord.configurationVersion;
    }

    public int getGeneral_profile_space() {
        return this.hevcDecoderConfigurationRecord.general_profile_space;
    }

    public boolean isGeneral_tier_flag() {
        return this.hevcDecoderConfigurationRecord.general_tier_flag;
    }

    public int getGeneral_profile_idc() {
        return this.hevcDecoderConfigurationRecord.general_profile_idc;
    }

    public long getGeneral_profile_compatibility_flags() {
        return this.hevcDecoderConfigurationRecord.general_profile_compatibility_flags;
    }

    public long getGeneral_constraint_indicator_flags() {
        return this.hevcDecoderConfigurationRecord.general_constraint_indicator_flags;
    }

    public int getGeneral_level_idc() {
        return this.hevcDecoderConfigurationRecord.general_level_idc;
    }

    public int getMin_spatial_segmentation_idc() {
        return this.hevcDecoderConfigurationRecord.min_spatial_segmentation_idc;
    }

    public int getParallelismType() {
        return this.hevcDecoderConfigurationRecord.parallelismType;
    }

    public int getChromaFormat() {
        return this.hevcDecoderConfigurationRecord.chromaFormat;
    }

    public int getBitDepthLumaMinus8() {
        return this.hevcDecoderConfigurationRecord.bitDepthLumaMinus8;
    }

    public int getBitDepthChromaMinus8() {
        return this.hevcDecoderConfigurationRecord.bitDepthChromaMinus8;
    }

    public int getAvgFrameRate() {
        return this.hevcDecoderConfigurationRecord.avgFrameRate;
    }

    public int getNumTemporalLayers() {
        return this.hevcDecoderConfigurationRecord.numTemporalLayers;
    }

    public int getLengthSizeMinusOne() {
        return this.hevcDecoderConfigurationRecord.lengthSizeMinusOne;
    }

    public boolean isTemporalIdNested() {
        return this.hevcDecoderConfigurationRecord.temporalIdNested;
    }

    public int getConstantFrameRate() {
        return this.hevcDecoderConfigurationRecord.constantFrameRate;
    }

    public List<HevcDecoderConfigurationRecord.Array> getArrays() {
        return this.hevcDecoderConfigurationRecord.arrays;
    }
}
