/*
 * Copyright 2023, Backblaze, Inc. All rights reserved.
 */
package org.rmurugaian.file.reader.job;

public record HGuidAndBzComb(
    String hGuid, String bzCombFileName, String serverSize, String clientSize) {}
