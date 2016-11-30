/*******************************************************************************
 * BSD License
 *  
 * Copyright (c) 2016, AT&T Intellectual Property.  All other rights reserved.
 *  
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *  
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions
 *    and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 *    conditions and the following disclaimer in the documentation and/or other materials provided
 *    with the distribution.
 * 3. All advertising materials mentioning features or use of this software must display the
 *    following acknowledgement:  This product includes software developed by the AT&T.
 * 4. Neither the name of AT&T nor the names of its contributors may be used to endorse or
 *    promote products derived from this software without specific prior written permission.
 *  
 * THIS SOFTWARE IS PROVIDED BY AT&T INTELLECTUAL PROPERTY ''AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL AT&T INTELLECTUAL PROPERTY BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;  LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 *******************************************************************************/
package com.att.mr.test.dmaap;



public class DmaapAdminTest {
	/*private static final Logger LOGGER = Logger.getLogger(DmaapAdminTest.class);
	Client client = ClientBuilder.newClient();
	WebTarget target = client.target(LoadPropertyFile.getPropertyFileData().getProperty("url"));


	public void assertStatus(Response response) {
		assertTrue(response.getStatus() == HttpStatus.SC_OK);
	}

	// 1.get consumer cache
	public void testConsumerCache() {
		LOGGER.info("test case consumer cache");
		target = target.path("/admin/consumerCache");
		Response response = target.request().get();
		assertStatus(response);
		LOGGER.info("Successfully returned after fetching consumer cache");
		InputStream is = (InputStream) response.getEntity();
		Scanner s = new Scanner(is);
		s.useDelimiter("\\A");
		String data = s.next();
		s.close();
		LOGGER.info("Details of consumer cache :" + data);
	}

	// 2.drop consumer cache
	public void testDropConsumerCache() {
		LOGGER.info("test case drop consumer cache");
		target = target.path("/admin/dropConsumerCache");
		Response response = target.request().post(Entity.json(null));
		assertStatus(response);
		LOGGER.info("Successfully returned after dropping consumer cache");
	}
*/
}
