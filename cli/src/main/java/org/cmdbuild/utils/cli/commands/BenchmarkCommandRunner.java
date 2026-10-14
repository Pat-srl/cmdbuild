/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cli.commands;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Math.round;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.cmdbuild.utils.benchmark.BenchmarkListener;
import org.cmdbuild.utils.benchmark.BenchmarkResult;
import org.cmdbuild.utils.benchmark.BenchmarkResults;
import org.cmdbuild.utils.benchmark.BenchmarkUtils;
import static org.cmdbuild.utils.io.CmNetUtils.getHostname;

public class BenchmarkCommandRunner extends AbstractCommandRunner {

    private boolean fail = false;

    public BenchmarkCommandRunner() {
        super("benchmark", "run a benchmark test to validate host performance");
    }

    @Override
    protected Options buildOptions() {
        Options options = super.buildOptions();
        options.addOption("m", true, "benchmark iterations (default to 1)");
        options.addOption("f", "fail if one test is in error");
        return options;
    }

    @Override
    protected void exec(CommandLine cmd) throws Exception {
        System.out.printf("\nPerforming benchmark test of this host (%s):\n\n", getHostname());

        int iterations;
        if (cmd.hasOption("m")) {
            iterations = Integer.parseInt(cmd.getOptionValue("m"));
            checkArgument(iterations > 0 && iterations < 100);
        } else {
            iterations = 1;
        }
        if (cmd.hasOption("f")) {
            fail = true;
        }
//
        System.out.printf("            test                 result       score\n");
        BenchmarkUtils.executeBenchmark(iterations, fail, new BenchmarkListener() {

            @Override
            public void onTestBegin(String name) {
                System.out.printf(" %-24s   ", name + " ...");
            }

            @Override
            public void onTestResult(BenchmarkResult result) {

                if (result.hasError()) {
                    logger.debug("test error for test = {}", result.getCategory(), result.getError());
                } else {
                    System.out.printf("     %6s        %3s\n", result.getResult(), round(result.getScore() * 100));
                }
            }

            @Override
            public void onTestEnd(BenchmarkResults results) {
                System.out.printf("\n  your average system score is %s, which is %s\n", round(results.getAverageScore() * 100), results.getAverageScore() < 1 ? "not good (a score of 100 or more is recommended to ensure good system performance)" : "good");
                if (results.getResults().stream().map(BenchmarkResult::getScore).anyMatch(i -> i < 1d)) {
                    System.out.printf("  some of your scores are below the recommended minimum score of 100\n");
                }
                System.out.printf("  to improve test results, you should stop all applications while you're running this test utility.\n");
            }
        });
    }

}
