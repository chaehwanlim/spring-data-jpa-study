package spring.datajpa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MemberDto {

    private Long id;
    private String username;
    private String teamName;

}
