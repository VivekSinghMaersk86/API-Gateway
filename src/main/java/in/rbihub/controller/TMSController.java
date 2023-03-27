package in.rbihub.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import in.rbihub.utils.TMSResponceDTO;
import in.rbihub.utils.TMSRequestDTO;
import java.util.Scanner;

@RestController
public class TMSController {

    @GetMapping("/checkXYZ")
    public String geturt() {
        return "Vivek";
    }

    @GetMapping("/TNSOutput")
    public TMSResponceDTO generateTNService(@RequestHeader("districtCode") String districtCode,
                                            @RequestHeader("talukCode") String talukCode,
                                            @RequestHeader("villCode") String villCode,
                                            @RequestHeader("surveyNo") String surveyNo,
                                            @RequestHeader("subdivNo") String subdivNo,
                                            @RequestHeader("lgd") boolean lgd) throws Exception {

        // List<ESignResponse>  YardResponseByENList1 = ESService.findByYardName(YardName);

        TMSResponceDTO TmsResEntity = new TMSResponceDTO();

        if(villCode.equals("642914")) {
            TmsResEntity.setOwnNum(101);
            TmsResEntity.setOwner("Jailalita");
            TmsResEntity.setRelation("Mother");
            return TmsResEntity;
        }else if (subdivNo.equals("1234")){
            TmsResEntity.setOwnNum(102);
            TmsResEntity.setOwner("PremSingh");
            TmsResEntity.setRelation("Father");
            return TmsResEntity;

        }else {
            return null;
        }
    }
}
