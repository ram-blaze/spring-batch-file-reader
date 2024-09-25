/*
 * Copyright 2024, Backblaze, Inc. All rights reserved.
 */
package org.rmurugaian.file.reader.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

/*
 * @author rmurugaian 2024-09-12
 */
@Entity
@Getter
@Setter
public class HGuidAndBzCombEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  private String hguid;
  private String bzcomb;
  private String server;
  private long serverSize;
  private long clientSize;
}
