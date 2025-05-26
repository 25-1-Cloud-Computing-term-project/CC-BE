package CC_BE.CC_BE.dto;

import CC_BE.CC_BE.domain.Manual;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ManualResponse {
    private Long id;
    private String fileName;

    public static ManualResponse from(Manual manual) {
        ManualResponse response = new ManualResponse();
        response.setId(manual.getId());
        response.setFileName(manual.getFileName());
        return response;
    }
} 