package com.example.task.Model;

public record GithubRepository(
        String name,
        boolean fork,
        Owner owner
){
    public record Owner(String login){}
}