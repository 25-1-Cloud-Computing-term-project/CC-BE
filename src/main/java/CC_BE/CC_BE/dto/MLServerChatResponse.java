package CC_BE.CC_BE.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class MLServerChatResponse {
    private String message;
    private String answer;
    private List<String> images;
} 