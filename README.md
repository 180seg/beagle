# Beagle
Beagle is an AWS policy judge (decision maker).

![beagle](https://user-images.githubusercontent.com/12648924/191837038-3eae3148-191e-4658-baa1-a0011b76e740.png)

Beagle acts like a decision maker being usefull in places where you need to evaluate if an AWS policy is to permissive, give access to sensitive data, give destructive permissions among other use cases.

To do that you need provide a set of laws to beagle and one or more policies, then he will check if your policies are breaking any laws.

# Concepts
Beagle is really simple and relies on two simple concepts.

## Laws
Set of aws policies containg the actions you do not allow to be gave. 

## Policies
One or more policies that are going to be judge by the defined laws.

# How Beagle takes his decision
Beagle uses AWS [simluate custom policy API](https://docs.aws.amazon.com/IAM/latest/APIReference/API_SimulateCustomPolicy.html) to determine if the provided policies are not breaking any law.

# Example
We have the following policy which allows an entity to perform the `PutObject` action in any bucket

```json
{
  "Version":"2012-10-17",
    "Statement":[
    {
      "Effect":"Allow",
      "Action":[
        "s3:PutObject"
      ],
      "Resource":"*"
    }
  ]
}
```

and the law above that does not allow any any action on s3 in a specific resource.

```json
{
  "Version":"2012-10-17",
    "Statement":[
    {
      "Effect":"Deny",
      "Action":[
        "s3:PutObject"
        "s3:GetObject"
      ],
      "Resource":[
        "arn:aws:s3:::bucket-with-secrets/*"
      ]
    }
  ]
}
```

Beagle will decide if the policy is not breaking the laws. 
To do that just save the previous policy on a file `policy.json` and the law in a directory of laws such as `laws/s3-secret-bucket.json` and then just run the following command.

```bash
clojure -m beagle.cli check --policy policy.json -l laws/
You can not trust in it.
```
