/**
 * Copyright (c) 2014, Franjo Žilić All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the
 * distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 */

package it.luigibifulco.crypto.tsa.rfc3161;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.tsp.TimeStampRequest;
import org.bouncycastle.tsp.TimeStampRequestGenerator;

import it.luigibifulco.crypto.tsa.rfc3161.config.Algorithm;
import it.luigibifulco.crypto.tsa.rfc3161.exceptions.Rfc3161Exception;

/**
 * 
 * @author Luigi Bifulco
 *
 */
public class RequestGenerator {

	private final Algorithm m_algorithm;

	private final TimeStampRequestGenerator m_generator = new TimeStampRequestGenerator();

	private final byte[] m_digest;

	private BigInteger m_nonce;

	private TimeStampRequest m_request;

	public RequestGenerator(final Algorithm p_algorithm, final byte[] p_digest) {
		m_algorithm = p_algorithm;
		m_digest = p_digest;
	}

	public RequestGenerator certReq(final boolean p_certReq) {
		m_generator.setCertReq(p_certReq);
		return this;

	}

	public void nonce(final BigInteger p_nonce) {
		m_nonce = p_nonce;
	}

	public void policyId(final String p_policyId) {
		m_generator.setReqPolicy(new ASN1ObjectIdentifier(p_policyId));
	}

	public TimeStampRequest request() {
		if (m_request == null) {
			if (m_nonce == null) {
				m_request = m_generator.generate(m_algorithm.getTspAlgorithm(), m_digest);
			} else {
				m_request = m_generator.generate(m_algorithm.getTspAlgorithm(), m_digest, m_nonce);
			}
		}

		return m_request;
	}

	public RequestGenerator storeRequest(final String p_digestFile) {
		if (m_request == null) {
			m_request = m_generator.generate(m_algorithm.getTspAlgorithm(), m_digest);
		}
		final File requestFile = new File(p_digestFile);

		try {
			final OutputStream output = new FileOutputStream(requestFile);
			IOUtils.write(m_request.getEncoded(), output);
			output.close();
		} catch (final IOException exception) {
			throw new Rfc3161Exception("Failed to save request to file: " + requestFile.getName(), exception);
		}

		return this;
	}

}
