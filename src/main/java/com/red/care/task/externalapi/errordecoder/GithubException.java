package com.red.care.task.externalapi.errordecoder;

public class GithubException {
    public static class GitHubClientException extends RuntimeException {
        public GitHubClientException(String message) {
            super(message);
        }
    }

    public static class GitHubNotFoundException extends RuntimeException {
        public GitHubNotFoundException(String message) {
            super(message);
        }
    }

    public static class GitHubRateLimitException extends RuntimeException {
        public GitHubRateLimitException(String message) {
            super(message);
        }
    }

    public static class GitHubServerException extends RuntimeException {
        public GitHubServerException(String message) {
            super(message);
        }
    }
}
