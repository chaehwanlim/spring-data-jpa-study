package spring.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import spring.datajpa.entity.Member;
import spring.datajpa.entity.Team;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    EntityManager em;

    @Test
    void test1() {
        Member member = Member.builder().username("memberA").build();
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).orElse(null);

        assert findMember != null;
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    void basicCRUD() {
        Member memberA = Member.builder().username("memberA").build();
        Member memberB = Member.builder().username("memberB").build();

        memberRepository.save(memberA);
        memberRepository.save(memberB);

        // 단일 조회 검증
        Member findMemberA = memberRepository.findById(memberA.getId()).get();
        Member findMemberB = memberRepository.findById(memberB.getId()).get();
        assertThat(findMemberA).isEqualTo(memberA);
        assertThat(findMemberB).isEqualTo(memberB);

        // 리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all).isEqualTo(List.of(memberA, memberB));

        // 카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(memberA);
        memberRepository.delete(memberA);
        long countAfterDeletion = memberRepository.count();
        assertThat(countAfterDeletion).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThan() {
        Member m1 = Member.builder().username("AAA").age(10).build();
        Member m2 = Member.builder().username("AAA").age(20).build();

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void testNamedQuery() {
        Member m1 = Member.builder().username("AAA").age(10).build();
        Member m2 = Member.builder().username("AAA").age(20).build();

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);

        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void testQuery() {
        Member m1 = Member.builder().username("AAA").age(10).build();
        Member m2 = Member.builder().username("AAA").age(20).build();

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        Member findMember = result.get(0);

        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void findUsernameList() {
        Member m1 = Member.builder().username("AAA").age(10).build();
        Member m2 = Member.builder().username("AAA").age(20).build();

        memberRepository.save(m1);
        memberRepository.save(m2);

        memberRepository.findUsernameList().forEach(username -> {
            System.out.println("username = " + username);
        });
    }

    @Test
    public void findMemberDto() {
        Team t1 = Team.builder().name("teamA").build();
        teamRepository.save(t1);

        Member m1 = Member.builder().username("AAA").age(10).build();
        m1.changeTeam(t1);
        memberRepository.save(m1);

        memberRepository.findMemberDto().forEach(memberDto -> {
            System.out.println("dto = " + memberDto);
        });
    }

    @Test
    public void findByNames() {
        Member m1 = Member.builder().username("AAA").age(10).build();
        Member m2 = Member.builder().username("BBB").age(20).build();

        memberRepository.save(m1);
        memberRepository.save(m2);

        memberRepository.findByNames(List.of("AAA", "BBB")).forEach(username -> {
            System.out.println("username = " + username);
        });
    }

    @Test
    public void returnTypeTest() {
        Member m1 = Member.builder().username("AAA").age(10).build();
        Member m2 = Member.builder().username("AAA").age(20).build();

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> aaa = memberRepository.findListByUsername("AAA");
        Member aaa1 = memberRepository.findMemberByUsername("AAA");
        Optional<Member> aaa2 = memberRepository.findOptionalMemberByUsername("AAA");
    }

    @Test
    public void pageTest() {
        // given
        for (int i = 1; i <= 5; i++) {
            memberRepository.save(Member.builder().username("member" + i).build());
        }

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // when
        // 반환 타입이 Page이므로 total count가 필요하므로 count 쿼리가 추가로 발생함
        Page<Member> page = memberRepository.findByAge(10, pageRequest);

        // then
        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

        assertThat(content.size()).isEqualTo(3);
        assertThat(totalElements).isEqualTo(5);
        assertThat(page.getNumber()).isZero();
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    public void bulkUpdate() {
        // given
        for (int i = 1; i <= 5; i++) {
            memberRepository.save(Member.builder().username("member" + i).age(7 * i).build());
        }

        //when
        int resultCount = memberRepository.bulkAgeIncrementMtAge(20);

        //then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy() {
        //given
        Team teamA = Team.builder().name("teamA").build();
        Team teamB = Team.builder().name("teamB").build();

        Member m1 = Member.builder().username("m1").team(teamA).age(24).build();
        Member m2 = Member.builder().username("m2").team(teamB).age(25).build();

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        memberRepository.save(m1);
        memberRepository.save(m2);

        em.flush();
        em.clear();

        //when
//        memberRepository.findAll().forEach(member -> {
        memberRepository.findMemberFetchJoin().forEach(member -> {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.teamClass = " + member.getTeam().getClass());
            System.out.println("member.teamName = " + member.getTeam().getName());
        });
    }

}