package spring.datajpa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import spring.datajpa.entity.Member;

@Getter
@Builder
@AllArgsConstructor
public class MemberDto {

    private Long id;
    private String username;
    private String teamName;

    public MemberDto(Member member) {
        this.id = member.getId();
        this.username = member.getUsername();
        this.teamName = member.getTeam().getName();
    }

}
