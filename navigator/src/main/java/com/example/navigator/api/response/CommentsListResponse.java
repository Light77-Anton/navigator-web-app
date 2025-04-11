package com.example.navigator.api.response;
import com.example.navigator.model.Comment;
import lombok.Data;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Data
public class CommentsListResponse {

    List<Comment> list;
}
