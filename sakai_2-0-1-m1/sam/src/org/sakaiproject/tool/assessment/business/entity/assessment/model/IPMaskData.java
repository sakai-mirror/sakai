/*
 * Copyright (c) 2003, 2004 The Regents of the University of Michigan, Trustees of Indiana University,
 *                  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation
 *
 * Licensed under the Educational Community License Version 1.0 (the "License");
 * By obtaining, using and/or copying this Original Work, you agree that you have read,
 * understand, and will comply with the terms and conditions of the Educational Community License.
 * You may obtain a copy of the License at:
 *
 *      http://cvs.sakaiproject.org/licenses/license_1_0.html
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.sakaiproject.tool.assessment.business.entity.assessment.model;

import java.net.InetAddress;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This holds the ip access model, and a list of allowed or blocked ip
 * addresses, based on the model.  Do we need a choice that combines allowed
 * and blocked in some way?  (i.e. everything on the allowed list except the
 * blocked ones, or nothing on the blocked list except the allowed ones?)
 *
 * @author Rachel Gollub
 * @author Ed Smiley
 */
public class IPMaskData
{
  private String ipAccessType;
  private Collection allowedAddresses;
  private Collection blockedAddresses;

  /**
   * Creates a new IPMaskData object.
   */
  public IPMaskData()
  {
    allowedAddresses = new ArrayList();
    blockedAddresses = new ArrayList();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getIPAccessType()
  {
    return ipAccessType;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pipAccessType DOCUMENTATION PENDING
   */
  public void setIPAccessType(String pipAccessType)
  {
    ipAccessType = pipAccessType;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Collection getAllowedAddresses()
  {
    return allowedAddresses;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pallowedAddresses DOCUMENTATION PENDING
   */
  public void setAllowedAddresses(Collection pallowedAddresses)
  {
    allowedAddresses = pallowedAddresses;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param paddress DOCUMENTATION PENDING
   */
  public void addAllowedAddress(InetAddress paddress)
  {
    allowedAddresses.add(paddress);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param paddress DOCUMENTATION PENDING
   */
  public void removeAllowedAddress(InetAddress paddress)
  {
    allowedAddresses.remove(paddress);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Collection getBlockedAddresses()
  {
    return blockedAddresses;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pblockedAddresses DOCUMENTATION PENDING
   */
  public void setBlockedAddresses(Collection pblockedAddresses)
  {
    blockedAddresses = pblockedAddresses;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param paddress DOCUMENTATION PENDING
   */
  public void addBlockedAddress(InetAddress paddress)
  {
    blockedAddresses.add(paddress);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param paddress DOCUMENTATION PENDING
   */
  public void removeBlockedAddress(InetAddress paddress)
  {
    blockedAddresses.remove(paddress);
  }
}
