package uk.gov.hmcts.ccd.v2.internal.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.ccd.domain.model.aggregated.UserProfile;
import uk.gov.hmcts.ccd.domain.service.aggregated.AuthorisedGetUserProfileOperation;
import uk.gov.hmcts.ccd.domain.service.aggregated.GetUserProfileOperation;
import uk.gov.hmcts.ccd.domain.service.common.AccessControlService;
import uk.gov.hmcts.ccd.v2.V2;
import uk.gov.hmcts.ccd.v2.internal.resource.UICaseViewResource;
import uk.gov.hmcts.ccd.v2.internal.resource.UIUserProfileResource;

@RestController
@RequestMapping(path = "/internal")
public class UIUserProfileController {

    private final GetUserProfileOperation getUserProfileOperation;

    @Autowired
    public UIUserProfileController(
        @Qualifier(AuthorisedGetUserProfileOperation.QUALIFIER) final GetUserProfileOperation getUserProfileOperation) {
        this.getUserProfileOperation = getUserProfileOperation;
    }

    @RequestMapping(
        method = RequestMethod.GET,
        path = "/profile",
        headers = {
            V2.EXPERIMENTAL_HEADER
        },
        produces = {
            V2.MediaType.UI_USER_PROFILE
        }
    )
    @ApiOperation(
        value = "Validate case data",
        notes = V2.EXPERIMENTAL_WARNING
    )
    @ApiResponses({
        @ApiResponse(
            code = 200,
            message = "Success",
            response = UICaseViewResource.class
        )
    })
    public ResponseEntity<UIUserProfileResource> getUserProfile() {

        UserProfile userProfile = getUserProfileOperation.execute(AccessControlService.CAN_READ);

        return ResponseEntity.ok(new UIUserProfileResource(userProfile));
    }
}
