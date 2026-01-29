package com.example.task.Model;

public record GithubBranch (
    String name,
    Commit commit
){
    public record Commit(String sha){}
}
