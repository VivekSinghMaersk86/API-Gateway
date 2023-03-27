package in.rbihub.utils;

public class TMSRequestDTO {
        String districtCode;
        String talukCode;
        String villCode;
        String surveyNo;
        String subdivNo;
        boolean lgd = true;

        public String getDistrictCode() {
                return districtCode;
        }

        public void setDistrictCode(String districtCode) {
                this.districtCode = districtCode;
        }

        public String getTalukCode() {
                return talukCode;
        }

        public void setTalukCode(String talukCode) {
                this.talukCode = talukCode;
        }

        public String getVillCode() {
                return villCode;
        }

        public void setVillCode(String villCode) {
                this.villCode = villCode;
        }

        public String getSurveyNo() {
                return surveyNo;
        }

        public void setSurveyNo(String surveyNo) {
                this.surveyNo = surveyNo;
        }

        public String getSubdivNo() {
                return subdivNo;
        }

        public void setSubdivNo(String subdivNo) {
                this.subdivNo = subdivNo;
        }

        public boolean isLgd() {
                return lgd;
        }

        public void setLgd(boolean lgd) {
                this.lgd = lgd;
        }
}
