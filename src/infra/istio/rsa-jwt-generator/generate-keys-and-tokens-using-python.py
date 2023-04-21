#
# Generates new public & private RSA keys and new token based on these keys
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


private_key = key.export_private()
public_key = key.export_public()


token = jwt.generate_jwt(payload, jwk.JWK.from_json(private_key), 'RS256', datetime.timedelta(minutes=50))


print("\n_________________PUBLIC___________________\n")
print(public_key)
print("\n_________________PRIVATE___________________\n")
print(private_key)
print("\n_________________TOKEN___________________\n")
print(token)


header, claims = jwt.verify_jwt(token, jwk.JWK.from_json(public_key), ['RS256'])


print("\n_________________TOKEN INFO___________________\n")
print(header)
print(claims)