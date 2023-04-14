#
# Generates new token based on the existing public & private RSA keys
#

import python_jwt as jwt, jwcrypto.jwk as jwk, datetime


key = jwk.JWK.generate(kty='RSA', size=2048)


payload = {
    'iss':'testing@secure.istio.io', 
    'sub':'SUBJECT', 
    'aud':'AUDIENCE', 
    'role': 'user', 
    'permission': 'read' 
}


private_key = '{"d":"IuTWFuyeXXaCW_ioHaXOQsStP-TyuHvmdv9DYvNq_6BShgqvtzWOA91vOFHsF4JQVoXg0W10EeojwVPoyepA5EJR-U7YAyCGDnDKSjknXcXgnx4E0YYtBRBMistmv_9hoJFzTBX-NQD7w7iwPLcvKTI66tZOfK2hA6Mp85NWmZYa4mBRVsDEmhUI2msZSXul1Km39CUVVdkTsCplaiGxnvHag8Y6EgeF94iHnuJvctR4bG4kJh2ji8zpTMv4aQF7YZl9iv8Pgq-81GPTAehYqJ8epX_343m3zR4iob0NH0ESolrz5jOKLmcsgmrdz-NTxU3dB-GCyQrZKe26nr9vAQ","dp":"THfbbAZ7NYRbkXnNBaWmxcQJL4QxBx-YKuiMWFIl3bWy-BKyN_uaJF8C16uc6SZuykbZF7qYDVZBKkuIPqH24xKsN-fjE09dZLacHm3TiZB1KVA1abuuKkHynBMC85FOuAyVN8RrOhLi-J47oDKdcPfvCH3MVjVFE_nVIFm4XjE","dq":"omR5zwauVnPOiH6SqS2FZENEBQH0P0dkkdM_XANpXfIiD4xx5VJElyelx52bHRZjn4SvD4T5VpLyVP_o0o0iOJUgtdJPedfpO0qNepsjqWb3T941f-ByLgPIUYX9mRa34IBwMFqEThkvPNo2sb41rpUy5XqL_ZGSJKunwIqfCUc","e":"AQAB","kty":"RSA","n":"qdk7FwUexAJuLKAYwvbyoBMqhoGlmSXa8L7xSZkiljHux9GQY0aGpMzMPXlQpSkRDVoJQ_XC1wxtvJvzyjYNW3qRB3gKE6Hd6GawDDv2F6041O1did40PC8vNnQ9qz1UuVPiv4e5oV9jHNVVIcHcaeUAtGzIiW7YBPJqmsu3O7kBkSMTBN5q3bGz-y1VSCA26bQfen3lIy4uY9suq2ySm9oMkT0dM5KYFfpYjge5VTP4biOzQP7RJtm23SVZ_57Y0OCfhR26v0EczdlbDvm36E3ap4cGqmw0X44pYYZydUYhBVOlnvOQc8cFzZfaHUBRFCKNfJORupoKHii-5gjv9w","p":"zIrhio6oE03tG3ZwhnAovkbPMKxdcdfuzpNLwCocEDYsId7m64zhD8INgbPgvvNuBS2_ZGWZdQmWKXI9WwpZeKpt7ME-7FnSt39duVNOU5FGGNG1xdrSrxkYle7Kn9g8XXHO9UlT8DlPj4Cn1EPOrbCyDDvGPY8LLrmZRZCa43E","q":"1JP2BCTyIh31hOgkUvCDuqVWKp_xHcC0xgIvMUVmfm4hpOxtqOZloUqs0Tw5Q0tWskiiDaxcM6A4p9omafLNIDSnWCy8_YRS34YkfA2XuJcSkiMdgnTpd8J3tdPJE_BGuVHaXAtAs9BEwRuqgShd8lI4VQg-wGxmz2_9XM3chec","qi":"xhXtkhZj9wt5pDk6DGFzLm54S08b1UvnWln8PhNE32k0-8BJnZPm1N6-pJISfbd2htSYXkgZgRHaqljYkFcJ_pUltBfUz3Gp_PXIKRQXTFplqgKkNmK8TGcswu-5_36kB20N2OW7e2zk8F25ZDUk3qLVkhz6uCl4hsKLb6B16II"}'

public_key = '{"e":"AQAB","kty":"RSA","n":"qdk7FwUexAJuLKAYwvbyoBMqhoGlmSXa8L7xSZkiljHux9GQY0aGpMzMPXlQpSkRDVoJQ_XC1wxtvJvzyjYNW3qRB3gKE6Hd6GawDDv2F6041O1did40PC8vNnQ9qz1UuVPiv4e5oV9jHNVVIcHcaeUAtGzIiW7YBPJqmsu3O7kBkSMTBN5q3bGz-y1VSCA26bQfen3lIy4uY9suq2ySm9oMkT0dM5KYFfpYjge5VTP4biOzQP7RJtm23SVZ_57Y0OCfhR26v0EczdlbDvm36E3ap4cGqmw0X44pYYZydUYhBVOlnvOQc8cFzZfaHUBRFCKNfJORupoKHii-5gjv9w"}'


token = jwt.generate_jwt(payload, jwk.JWK.from_json(private_key), 'RS256', datetime.timedelta(minutes=50))


# Print the public key, private key and the token
# print("\n_________________PUBLIC___________________\n")
# print(public_key)
# print("\n_________________PRIVATE___________________\n")
# print(private_key)
print("\n_________________TOKEN___________________\n")
print(token)


header, claims = jwt.verify_jwt(token, jwk.JWK.from_json(public_key), ['RS256'])


print("\n_________________TOKEN INFO___________________\n")
print(header)
print(claims)