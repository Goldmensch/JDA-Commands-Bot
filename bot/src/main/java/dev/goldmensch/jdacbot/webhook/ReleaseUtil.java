package dev.goldmensch.jdacbot.webhook;

import dev.goldmensch.jdacbot.webhook.pojo.Release;

import java.util.List;

final class ReleaseUtil {

    static String extractSummary(String release) {
        List<String> lines = release.lines().toList();

        int start = 0;
        int end = 0;
        for (int i = 0; i < lines.size(); i++) {
            String c = lines.get(i);
            if (c.startsWith("# Summary")) {
                start = i+1;
            } else if (c.startsWith("#")) {
                end = i;
                break;
            }
        }

        List<String> summary = lines.subList(start, end);
        return String.join(System.lineSeparator(), summary);
    }

    static String buildReleaseMessage(Release release) {
        return  """
                    ## New Release: %s
                    
                    %s
                    
                    To learn more, visit release page [here](%s)!
                    """.formatted(release.name(), ReleaseUtil.extractSummary(release.body()), release.htmlUrl());
    }
}
