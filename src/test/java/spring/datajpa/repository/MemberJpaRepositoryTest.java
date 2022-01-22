package spring.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spring.datajpa.entity.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MemberJpaRepositoryTest {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Test
    public void test1() {
        Member member = Member.builder().username("memberA").build();
        Member savedMember = memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.find(savedMember.getId());

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD() {
        Member memberA = Member.builder().username("memberA").build();
        Member memberB = Member.builder().username("memberB").build();

        memberJpaRepository.save(memberA);
        memberJpaRepository.save(memberB);

        // 단일 조회 검증
        Member findMemberA = memberJpaRepository.findById(memberA.getId()).get();
        Member findMemberB = memberJpaRepository.findById(memberB.getId()).get();
        assertThat(findMemberA).isEqualTo(memberA);
        assertThat(findMemberB).isEqualTo(memberB);

        // 리스트 조회 검증
        List<Member> all = memberJpaRepository.findAll();
        assertThat(all).isEqualTo(List.of(memberA, memberB));

        // 카운트 검증
        long count = memberJpaRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberJpaRepository.delete(memberA);
        memberJpaRepository.delete(memberA);
        long countAfterDeletion = memberJpaRepository.count();
        assertThat(countAfterDeletion).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThan() {
        Member m1 = Member.builder().username("AAA").age(10).build();
        Member m2 = Member.builder().username("AAA").age(20).build();

        memberJpaRepository.save(m1);
        memberJpaRepository.save(m2);

        List<Member> result = memberJpaRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void testNamedQuery() {
        Member m1 = Member.builder().username("AAA").age(10).build();
        Member m2 = Member.builder().username("AAA").age(20).build();

        memberJpaRepository.save(m1);
        memberJpaRepository.save(m2);

        List<Member> result = memberJpaRepository.findByUsername("AAA");
        Member findMember = result.get(0);

        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void paging() {
        // given
        for (int i = 1; i <= 5; i++) {
            memberJpaRepository.save(Member.builder().username("member" + i).build());
        }

        int age = 10;
        int offset = 0;
        int limit = 3;

        // when
        List<Member> members = memberJpaRepository.findByPage(age, offset, limit);
        long totalCount = memberJpaRepository.totalCount(age);

        // then
        assertThat(members.size()).isEqualTo(3);
        assertThat(totalCount).isEqualTo(5);
    }

    @Test
    public void bulkUpdate() {
        // given
        for (int i = 1; i <= 5; i++) {
            memberJpaRepository.save(Member.builder().username("member" + i).age(7 * i).build());
        }

        //when
        int resultCount = memberJpaRepository.bulkAgeIncrementMtAge(20);

        //then
        assertThat(resultCount).isEqualTo(3);
    }

}