package spring.datajpa.repository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UsernameOnlyDto {

    private final String username;

    public String getUsername() {
        return username;
    }

}
